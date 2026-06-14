<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">临时离院管理</div>
    </div>
    <div class="page-content">
      <div class="filter-bar">
        <el-select v-model="queryWardId" placeholder="病区" clearable style="width:160px">
          <el-option v-for="w in wardList" :key="w.id" :label="w.wardName" :value="w.id" />
        </el-select>
        <el-select v-model="queryLeaveStatus" placeholder="离院状态" clearable style="width:140px">
          <el-option label="离院中" :value="1" />
          <el-option label="已返回" :value="2" />
          <el-option label="已失效" :value="3" />
        </el-select>
        <el-input v-model="queryPersonName" placeholder="陪护人姓名" clearable style="width:140px" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="warning" @click="showLeaveDialog">办理临时离院</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="leaveNo" label="离院单号" width="160" />
        <el-table-column prop="certNo" label="陪护证号" width="150" />
        <el-table-column prop="personName" label="陪护人" width="90" />
        <el-table-column prop="patientName" label="患者" width="80" />
        <el-table-column prop="wardName" label="病区" width="120" />
        <el-table-column prop="leaveType" label="离院类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.leaveType === 1 ? 'primary' : 'danger'">
              {{ row.leaveType === 1 ? '临时离院' : '超时未归' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="leaveTime" label="离院时间" width="170" />
        <el-table-column prop="expectedReturnTime" label="预计返回" width="170" />
        <el-table-column prop="actualReturnTime" label="实际返回" width="170" />
        <el-table-column prop="leaveStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.leaveStatus === 1 ? 'warning' : row.leaveStatus === 2 ? 'success' : 'danger'">
              {{ row.leaveStatus === 1 ? '离院中' : row.leaveStatus === 2 ? '已返回' : '已失效' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.leaveStatus === 1" type="success" size="small" @click="handleReturn(row)">
              办理返回
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="current" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog v-model="leaveDialogVisible" title="办理临时离院" width="450px" destroy-on-close>
      <el-form :model="leaveForm" label-width="120px" :rules="leaveRules" ref="leaveFormRef">
        <el-form-item label="陪护证" prop="certId">
          <el-select v-model="leaveForm.certId" placeholder="选择有效陪护证" filterable style="width:100%">
            <el-option v-for="c in validCerts" :key="c.id"
              :label="`${c.certNo} - ${c.personName}(${c.patientName})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="离院类型" prop="leaveType">
          <el-radio-group v-model="leaveForm.leaveType">
            <el-radio :value="1">临时离院</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="预计返回时间" prop="expectedReturnTime">
          <el-date-picker v-model="leaveForm.expectedReturnTime" type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="leaveDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitLeave" :loading="submitting">确认离院</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { temporaryLeave, returnFromLeave, getLeavePage, getCertificatePage } from '@/api/certificate'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const queryWardId = ref(null)
const queryLeaveStatus = ref(null)
const queryPersonName = ref('')
const wardList = ref([])
const validCerts = ref([])
const leaveDialogVisible = ref(false)
const leaveFormRef = ref(null)

const leaveForm = ref({ certId: null, leaveType: 1, expectedReturnTime: '' })
const leaveRules = {
  certId: [{ required: true, message: '请选择陪护证', trigger: 'change' }],
  leaveType: [{ required: true, message: '请选择离院类型', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getLeavePage({
      current: current.value, size: size.value,
      wardId: queryWardId.value, leaveStatus: queryLeaveStatus.value, personName: queryPersonName.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const loadValidCerts = async () => {
  const res = await getCertificatePage({ current: 1, size: 200, certStatus: 1 })
  validCerts.value = res.data.records
}

const showLeaveDialog = () => {
  leaveForm.value = { certId: null, leaveType: 1, expectedReturnTime: '' }
  loadValidCerts()
  leaveDialogVisible.value = true
}

const submitLeave = async () => {
  await leaveFormRef.value.validate()
  submitting.value = true
  try {
    const userInfo = localStorage.getItem('userInfo')
    const user = userInfo ? JSON.parse(userInfo) : {}
    await temporaryLeave({ ...leaveForm.value, operatorId: user.userId, operatorName: user.realName })
    ElMessage.success('临时离院办理成功')
    leaveDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleReturn = async (row) => {
  await ElMessageBox.confirm('确认该陪护人已返回？', '办理返回')
  const userInfo = localStorage.getItem('userInfo')
  const user = userInfo ? JSON.parse(userInfo) : {}
  await returnFromLeave({ leaveRecordId: row.id, operatorId: user.userId, operatorName: user.realName })
  ElMessage.success('返回登记成功')
  loadData()
}

onMounted(async () => {
  const res = await getWardList()
  wardList.value = res.data
  loadData()
})
</script>
