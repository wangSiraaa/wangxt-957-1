<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">提交陪护申请</div>
      <el-button @click="goBack">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </el-button>
    </div>

    <el-form :model="form" :rules="rules" ref="formRef" label-width="120px" class="submit-form">
      <el-divider content-position="left">患者信息</el-divider>

      <el-form-item label="选择患者" prop="patientId">
        <el-select v-model="form.patientId" filterable placeholder="请搜索选择患者" style="width: 300px"
          @change="onPatientChange">
          <el-option v-for="patient in patientList" :key="patient.id"
            :label="`${patient.patientName} - ${patient.patientNo}`" :value="patient.id">
            <span>{{ patient.patientName }}</span>
            <span style="float: right; color: #8492a6; font-size: 12px">{{ patient.patientNo }}</span>
          </el-option>
        </el-select>
        <el-button style="margin-left: 10px" @click="searchPatient">
          <el-icon><Search /></el-icon>
          搜索患者
        </el-button>
      </el-form-item>

      <div v-if="selectedPatient" class="patient-info">
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="患者姓名">{{ selectedPatient.patientName }}</el-descriptions-item>
          <el-descriptions-item label="住院号">{{ selectedPatient.patientNo }}</el-descriptions-item>
          <el-descriptions-item label="病区">
            <el-tag :type="wardInfo?.isIsolation ? 'danger' : 'info'">
              {{ wardInfo?.wardName }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="床号">{{ selectedPatient.bedNo }}</el-descriptions-item>
          <el-descriptions-item label="诊断">{{ selectedPatient.diagnosis }}</el-descriptions-item>
          <el-descriptions-item label="入院日期">{{ selectedPatient.admissionDate }}</el-descriptions-item>
        </el-descriptions>
        <el-alert v-if="wardInfo?.isIsolation" title="隔离病区不能新增陪护" type="error" show-icon style="margin-top: 10px" />
      </div>

      <el-divider content-position="left">陪护人员信息</el-divider>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="姓名" prop="personName">
            <el-input v-model="form.personName" placeholder="请输入姓名" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="身份证号" prop="idCard">
            <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="性别" prop="gender">
            <el-radio-group v-model="form.gender">
              <el-radio :value="1">男</el-radio>
              <el-radio :value="2">女</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="联系电话" prop="phone">
            <el-input v-model="form.phone" placeholder="请输入联系电话" maxlength="11" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="与患者关系" prop="relation">
            <el-input v-model="form.relation" placeholder="如：父子、夫妻、兄弟等" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="身份证有效期" prop="idCardExpireDate">
            <el-date-picker v-model="form.idCardExpireDate" type="date" placeholder="选择有效期" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="住址">
        <el-input v-model="form.address" placeholder="请输入住址" />
      </el-form-item>

      <el-divider content-position="left">陪护信息</el-divider>

      <el-row :gutter="20">
        <el-col :span="8">
          <el-form-item label="开始日期" prop="expectedStartDate">
            <el-date-picker v-model="form.expectedStartDate" type="date" placeholder="选择开始日期"
              style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="结束日期" prop="expectedEndDate">
            <el-date-picker v-model="form.expectedEndDate" type="date" placeholder="选择结束日期" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item label="申请理由">
        <el-input v-model="form.applyReason" type="textarea" :rows="3" placeholder="请输入申请理由" />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">提交申请</el-button>
        <el-button @click="goBack">取消</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { submitApply } from '@/api/apply'
import { getPatientPage } from '@/api/patient'
import { getWardList } from '@/api/ward'

const router = useRouter()
const formRef = ref(null)
const submitting = ref(false)
const patientList = ref([])
const selectedPatient = ref(null)
const wardInfo = ref(null)
const wardList = ref([])

const form = reactive({
  patientId: null,
  personName: '',
  idCard: '',
  gender: 1,
  phone: '',
  relation: '',
  idCardExpireDate: null,
  address: '',
  applyReason: '',
  expectedStartDate: null,
  expectedEndDate: null
})

const rules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  personName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: '身份证号格式不正确', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  relation: [{ required: true, message: '请输入与患者关系', trigger: 'blur' }],
  idCardExpireDate: [{ required: true, message: '请选择身份证有效期', trigger: 'change' }],
  expectedStartDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  expectedEndDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const searchPatient = async () => {
  try {
    const res = await getPatientPage({ current: 1, size: 20 })
    patientList.value = res.data.records
  } catch (error) {
    console.error('搜索患者失败：', error)
  }
}

const onPatientChange = async (val) => {
  const patient = patientList.value.find(p => p.id === val)
  if (patient) {
    selectedPatient.value = patient
    if (wardList.value.length === 0) {
      const res = await getWardList()
      wardList.value = res.data
    }
    wardInfo.value = wardList.value.find(w => w.id === patient.wardId)
  }
}

const handleSubmit = async () => {
  try {
    await formRef.value.validate()

    if (wardInfo.value?.isIsolation) {
      ElMessage.error('隔离病区不能新增陪护')
      return
    }

    await ElMessageBox.confirm('确认提交陪护申请？', '提示', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    submitting.value = true
    const res = await submitApply(form)
    ElMessage.success('申请提交成功，申请单号：' + res.data)
    router.push('/apply')
  } catch (error) {
    if (error !== 'cancel') {
      console.error('提交失败：', error)
    }
  } finally {
    submitting.value = false
  }
}

const goBack = () => {
  router.back()
}
</script>

<style scoped>
.submit-form {
  max-width: 900px;
}

.patient-info {
  margin: 10px 0 20px 120px;
}
</style>
