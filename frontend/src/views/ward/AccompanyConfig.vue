<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">陪护人数配置</div>
    </div>
    <div class="page-content">
      <div class="filter-bar">
        <el-select v-model="queryWardId" placeholder="病区" clearable style="width:160px">
          <el-option v-for="w in wardList" :key="w.id" :label="w.wardName" :value="w.id" />
        </el-select>
        <el-input v-model="queryPatientName" placeholder="患者姓名" clearable style="width:140px" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="showAdjustDialog">调整人数上限</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="patientName" label="患者" width="90" />
        <el-table-column prop="patientNo" label="住院号" width="120" />
        <el-table-column prop="bedNo" label="床号" width="80" />
        <el-table-column prop="wardName" label="病区" width="120" />
        <el-table-column prop="maxAccompanyCount" label="陪护人数上限" width="120" />
        <el-table-column prop="adjustReason" label="调整原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="operatorName" label="操作人" width="90" />
        <el-table-column prop="updateTime" label="更新时间" width="170" />
      </el-table>
      <el-pagination v-model:current-page="current" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog v-model="adjustDialogVisible" title="调整陪护人数上限" width="450px" destroy-on-close>
      <el-form :model="adjustForm" label-width="120px" :rules="adjustRules" ref="adjustFormRef">
        <el-form-item label="患者" prop="patientId">
          <el-select v-model="adjustForm.patientId" placeholder="选择患者" filterable style="width:100%"
            @change="onPatientChange">
            <el-option v-for="p in patientList" :key="p.id"
              :label="`${p.patientName}(${p.patientNo})`" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="陪护人数上限" prop="maxAccompanyCount">
          <el-input-number v-model="adjustForm.maxAccompanyCount" :min="0" :max="10" />
        </el-form-item>
        <el-form-item label="调整原因" prop="adjustReason">
          <el-input v-model="adjustForm.adjustReason" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="adjustDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitAdjust" :loading="submitting">确认调整</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { adjustAccompanyCount, getConfigPage } from '@/api/wardPatientConfig'
import { getPatientPage } from '@/api/patient'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const queryWardId = ref(null)
const queryPatientName = ref('')
const wardList = ref([])
const patientList = ref([])
const adjustDialogVisible = ref(false)
const adjustFormRef = ref(null)

const adjustForm = ref({ patientId: null, maxAccompanyCount: 1, adjustReason: '' })
const adjustRules = {
  patientId: [{ required: true, message: '请选择患者', trigger: 'change' }],
  maxAccompanyCount: [{ required: true, message: '请输入人数上限', trigger: 'change' }],
  adjustReason: [{ required: true, message: '请输入调整原因', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getConfigPage({
      current: current.value, size: size.value,
      wardId: queryWardId.value, patientName: queryPatientName.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const showAdjustDialog = async () => {
  adjustForm.value = { patientId: null, maxAccompanyCount: 1, adjustReason: '' }
  const res = await getPatientPage({ current: 1, size: 200 })
  patientList.value = res.data.records
  adjustDialogVisible.value = true
}

const onPatientChange = () => {}

const submitAdjust = async () => {
  await adjustFormRef.value.validate()
  submitting.value = true
  try {
    const userInfo = localStorage.getItem('userInfo')
    const user = userInfo ? JSON.parse(userInfo) : {}
    await adjustAccompanyCount({ ...adjustForm.value, operatorId: user.userId, operatorName: user.realName })
    ElMessage.success('陪护人数上限调整成功')
    adjustDialogVisible.value = false
    loadData()
  } finally { submitting.value = false }
}

onMounted(async () => {
  const res = await getWardList()
  wardList.value = res.data
  loadData()
})
</script>
