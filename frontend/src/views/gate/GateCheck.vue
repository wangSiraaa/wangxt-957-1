<template>
  <div class="gate-check-container">
    <div class="page-container">
      <div class="page-header">
        <div class="page-title">门禁查验</div>
        <div class="gate-type-switch">
          <el-radio-group v-model="gateType" size="large">
            <el-radio-button :value="1">入院查验</el-radio-button>
            <el-radio-button :value="2">出院查验</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <div class="check-section">
        <div class="check-input">
          <el-input v-model="certNoOrIdCard" placeholder="请输入陪护证号或身份证号" size="large"
            @keyup.enter="handleCheck" clearable>
            <template #append>
              <el-button type="primary" :loading="checking" @click="handleCheck">
                <el-icon><Search /></el-icon>
                查验
              </el-button>
            </template>
          </el-input>
        </div>

        <div v-if="checkResult" class="check-result" :class="checkResult.checkResult === 1 ? 'success' : 'fail'">
          <div class="result-icon">
            <el-icon :size="80">
              <CircleCheckFilled v-if="checkResult.checkResult === 1" />
              <CircleCloseFilled v-else />
            </el-icon>
          </div>
          <div class="result-text">
            {{ checkResult.checkResult === 1 ? '查验通过' : '查验拒绝' }}
          </div>
          <div v-if="checkResult.refuseReason" class="refuse-reason">
            {{ checkResult.refuseReason }}
          </div>
        </div>

        <div v-if="certInfo" class="cert-info">
          <el-descriptions :column="2" border title="陪护证信息">
            <el-descriptions-item label="证件编号">{{ certInfo.certNo }}</el-descriptions-item>
            <el-descriptions-item label="证件状态">
              <el-tag :type="certInfo.certStatus === 1 ? 'success' : 'info'">
                {{ certInfo.certStatus === 1 ? '有效' : '已失效' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="陪护人员">{{ certInfo.personName }}</el-descriptions-item>
            <el-descriptions-item label="身份证号">{{ certInfo.idCard }}</el-descriptions-item>
            <el-descriptions-item label="患者姓名">{{ certInfo.patientName }}</el-descriptions-item>
            <el-descriptions-item label="住院号">{{ certInfo.patientNo }}</el-descriptions-item>
            <el-descriptions-item label="病区">{{ certInfo.wardName }}</el-descriptions-item>
            <el-descriptions-item label="床号">{{ certInfo.bedNo }}</el-descriptions-item>
            <el-descriptions-item label="有效期开始">{{ certInfo.startDate }}</el-descriptions-item>
            <el-descriptions-item label="有效期结束">{{ certInfo.endDate }}</el-descriptions-item>
            <el-descriptions-item label="发证时间">{{ certInfo.issueTime }}</el-descriptions-item>
            <el-descriptions-item label="发证人员">{{ certInfo.issueUserName }}</el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="quick-buttons">
          <el-button size="large" @click="quickEntry">
            <el-icon><Right /></el-icon>
            快速入院
          </el-button>
          <el-button size="large" type="danger" @click="quickExit">
            <el-icon><Left /></el-icon>
            快速出院
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { gateCheck } from '@/api/gate'

const checking = ref(false)
const gateType = ref(1)
const certNoOrIdCard = ref('')
const certInfo = ref(null)
const checkResult = ref(null)

const handleCheck = async () => {
  if (!certNoOrIdCard.value.trim()) {
    ElMessage.warning('请输入陪护证号或身份证号')
    return
  }

  try {
    checking.value = true
    const userInfo = localStorage.getItem('userInfo')
    let userId = null
    let userName = null
    if (userInfo) {
      const user = JSON.parse(userInfo)
      userId = user.userId
      userName = user.realName
    }

    const res = await gateCheck({
      certNoOrIdCard: certNoOrIdCard.value,
      gateType: gateType.value,
      gateUserId: userId,
      gateUserName: userName
    })

    checkResult.value = res.data
    certInfo.value = null

    if (res.data.checkResult === 1) {
      ElMessage.success(gateType.value === 1 ? '入院查验通过' : '出院查验通过')
    }
  } catch (error) {
    console.error('查验失败：', error)
  } finally {
    checking.value = false
  }
}

const quickEntry = () => {
  gateType.value = 1
  handleCheck()
}

const quickExit = () => {
  gateType.value = 2
  handleCheck()
}
</script>

<style scoped>
.gate-check-container {
  min-height: 100%;
}

.check-section {
  max-width: 700px;
  margin: 40px auto;
}

.check-input {
  margin-bottom: 30px;
}

.check-result {
  text-align: center;
  padding: 40px;
  border-radius: 12px;
  margin-bottom: 30px;
}

.check-result.success {
  background: linear-gradient(135deg, #f0f9eb 0%, #e1f3d8 100%);
}

.check-result.fail {
  background: linear-gradient(135deg, #fef0f0 0%, #fde2e2 100%);
}

.result-icon {
  margin-bottom: 16px;
}

.result-icon :deep(.el-icon) {
  font-size: 80px;
}

.check-result.success .result-icon {
  color: #67c23a;
}

.check-result.fail .result-icon {
  color: #f56c6c;
}

.result-text {
  font-size: 24px;
  font-weight: 600;
  margin-bottom: 10px;
}

.check-result.success .result-text {
  color: #67c23a;
}

.check-result.fail .result-text {
  color: #f56c6c;
}

.refuse-reason {
  font-size: 14px;
  color: #909399;
}

.cert-info {
  margin-bottom: 30px;
}

.quick-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}
</style>
