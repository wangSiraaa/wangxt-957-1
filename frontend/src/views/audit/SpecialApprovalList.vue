<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">隔离病区特殊审批</div>
    </div>
    <div class="page-content">
      <div class="filter-bar">
        <el-select v-model="queryWardId" placeholder="病区" clearable style="width:160px">
          <el-option v-for="w in isolationWards" :key="w.id" :label="w.wardName" :value="w.id" />
        </el-select>
        <el-select v-model="queryApprovalStatus" placeholder="审批状态" clearable style="width:120px">
          <el-option label="待审核" :value="0" />
          <el-option label="已通过" :value="1" />
          <el-option label="已拒绝" :value="2" />
        </el-select>
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="warning" @click="showApplyDialog">发起特殊审批</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="approvalNo" label="审批单号" width="160" />
        <el-table-column prop="patientName" label="患者" width="80" />
        <el-table-column prop="wardName" label="病区" width="120" />
        <el-table-column prop="personName" label="陪护人" width="90" />
        <el-table-column prop="idCard" label="身份证号" width="170" />
        <el-table-column prop="approvalReason" label="审批原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="approvalStatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.approvalStatus === 0 ? 'warning' : row.approvalStatus === 1 ? 'success' : 'danger'">
              {{ row.approvalStatus === 0 ? '待审核' : row.approvalStatus === 1 ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applyUserName" label="申请人" width="90" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.approvalStatus === 0" type="primary" size="small" @click="handleAudit(row, 1)">
              通过
            </el-button>
            <el-button v-if="row.approvalStatus === 0" type="danger" size="small" @click="handleAudit(row, 2)">
              拒绝
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="current" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog v-model="applyDialogVisible" title="发起特殊审批" width="500px" destroy-on-close>
      <el-alert title="特殊审批仅适用于隔离病区" type="info" :closable="false" show-icon style="margin-bottom:16px" />
      <el-form :model="applyForm" label-width="100px" :rules="applyRules" ref="applyFormRef">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="applyForm.patientId" placeholder="选择隔离病区患者" filterable style="width:100%">
            <el-option v-for="p in isolationPatients" :key="p.id"
              :label="`${p.patientName}(${p.patientNo})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="陪护人" prop="personId">
          <el-select v-model="applyForm.personId" placeholder="选择或输入陪护人" filterable allow-create style="width:100%">
            <el-option v-for="p in personList" :key="p.id" :label="p.personName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="审批原因" prop="approvalReason">
          <el-input v-model="applyForm.approvalReason" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="开始日期" prop="startDate">
          <el-date-picker v-model="applyForm.startDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="结束日期" prop="endDate">
          <el-date-picker v-model="applyForm.endDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitApply" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { submitSpecialApproval, auditSpecialApproval, getSpecialApprovalPage } from '@/api/specialApproval'
import { getPatientPage } from '@/api/patient'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const queryWardId = ref(null)
const queryApprovalStatus = ref(null)
const wardList = ref([])
const isolationWards = ref([])
const isolationPatients = ref([])
const personList = ref([])
const applyDialogVisible = ref(false)
const applyFormRef = ref(null)

const applyForm = ref({ patientId: null, personId: null, approvalReason: '', startDate: '', endDate: '' })
const applyRules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  personId: [{ required: true, message: '请选择陪护人', trigger: 'change' }],
  approvalReason: [{ required: true, message: '请输入审批原因', trigger: 'blur' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getSpecialApprovalPage({
      current: current.value, size: size.value,
      wardId: queryWardId.value, approvalStatus: queryApprovalStatus.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const showApplyDialog = async () => {
  applyForm.value = { patientId: null, personId: null, approvalReason: '', startDate: '', endDate: '' }
  for (const w of isolationWards.value) {
    const res = await getPatientPage({ current: 1, size: 200, wardId: w.id })
    isolationPatients.value.push(...res.data.records)
  }
  applyDialogVisible.value = true
}

const submitApply = async () => {
  await applyFormRef.value.validate()
  submitting.value = true
  try {
    const userInfo = localStorage.getItem('userInfo')
    const user = userInfo ? JSON.parse(userInfo) : {}
    await submitSpecialApproval({ ...applyForm.value, applyUserId: user.userId, applyUserName: user.realName })
    ElMessage.success('特殊审批已提交')
    applyDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

const handleAudit = async (row, result) => {
  const action = result === 1 ? '通过' : '拒绝'
  await ElMessageBox.confirm(`确认${action}该审批？`, '确认')
  const userInfo = localStorage.getItem('userInfo')
  const user = userInfo ? JSON.parse(userInfo) : {}
  await auditSpecialApproval({
    approvalId: row.id, auditResult: result,
    auditUserId: user.userId, auditUserName: user.realName
  })
  ElMessage.success(`审批已${action}`)
  loadData()
}

onMounted(async () => {
  const res = await getWardList()
  wardList.value = res.data
  isolationWards.value = res.data.filter(w => w.isIsolation === 1)
  loadData()
})
</script>
