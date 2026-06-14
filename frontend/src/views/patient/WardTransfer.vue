<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">病区转床</div>
    </div>
    <div class="page-content">
      <div class="filter-bar">
        <el-input v-model="queryPatientName" placeholder="患者姓名" clearable style="width:140px" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="warning" @click="showTransferDialog">发起转床</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="transferNo" label="转床单号" width="160" />
        <el-table-column prop="patientName" label="患者" width="80" />
        <el-table-column prop="patientNo" label="住院号" width="120" />
        <el-table-column prop="fromWardName" label="原病区" width="120" />
        <el-table-column prop="fromBedNo" label="原床号" width="80" />
        <el-table-column prop="toWardName" label="目标病区" width="120" />
        <el-table-column prop="toBedNo" label="新床号" width="80" />
        <el-table-column prop="operatorName" label="操作人" width="90" />
        <el-table-column prop="transferTime" label="转床时间" width="170" />
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      </el-table>
      <el-pagination v-model:current-page="current" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog v-model="transferDialogVisible" title="发起病区转床" width="500px" destroy-on-close>
      <el-alert title="注意：转床后原病区陪护证将自动失效，需重新申请" type="warning" :closable="false" show-icon style="margin-bottom:16px" />
      <el-form :model="transferForm" label-width="100px" :rules="transferRules" ref="transferFormRef">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="transferForm.patientId" placeholder="选择患者" filterable style="width:100%"
            @change="onPatientChange">
            <el-option v-for="p in patientList" :key="p.id"
              :label="`${p.patientName}(${p.patientNo}) - ${p.bedNo || '无床号'}`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标病区" prop="toWardId">
          <el-select v-model="transferForm.toWardId" placeholder="选择目标病区" style="width:100%">
            <el-option v-for="w in wardList" :key="w.id" :label="w.wardName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="新床号">
          <el-input v-model="transferForm.toBedNo" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="transferForm.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer" :loading="submitting">确认转床</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPatientPage } from '@/api/patient'
import { getWardList } from '@/api/ward'
import { transferWard, getTransferPage } from '@/api/patientTransfer'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const queryPatientName = ref('')
const wardList = ref([])
const patientList = ref([])
const transferDialogVisible = ref(false)
const transferFormRef = ref(null)

const transferForm = ref({ patientId: null, toWardId: null, toBedNo: '', remark: '' })
const transferRules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  toWardId: [{ required: true, message: '请选择目标病区', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTransferPage({
      current: current.value, size: size.value, patientName: queryPatientName.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const loadPatients = async () => {
  const res = await getPatientPage({ current: 1, size: 200 })
  patientList.value = res.data.records
}

const onPatientChange = () => {}

const showTransferDialog = () => {
  transferForm.value = { patientId: null, toWardId: null, toBedNo: '', remark: '' }
  loadPatients()
  transferDialogVisible.value = true
}

const submitTransfer = async () => {
  await transferFormRef.value.validate()
  await ElMessageBox.confirm('确认转床？转床后原陪护证将自动失效！', '确认')
  submitting.value = true
  try {
    const userInfo = localStorage.getItem('userInfo')
    const user = userInfo ? JSON.parse(userInfo) : {}
    await transferWard({ ...transferForm.value, operatorId: user.userId, operatorName: user.realName })
    ElMessage.success('转床成功，原陪护证已自动失效')
    transferDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

onMounted(async () => {
  const res = await getWardList()
  wardList.value = res.data
  loadData()
})
</script>
