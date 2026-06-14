<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">换陪护管理</div>
    </div>
    <div class="page-content">
      <div class="filter-bar">
        <el-select v-model="queryWardId" placeholder="病区" clearable style="width:160px">
          <el-option v-for="w in wardList" :key="w.id" :label="w.wardName" :value="w.id" />
        </el-select>
        <el-input v-model="queryOldPerson" placeholder="原陪护人" clearable style="width:140px" />
        <el-input v-model="queryNewPerson" placeholder="新陪护人" clearable style="width:140px" />
        <el-button type="primary" @click="loadData">查询</el-button>
        <el-button type="success" @click="showTransferDialog">发起换陪护</el-button>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="transferNo" label="交接单号" width="160" />
        <el-table-column prop="patientName" label="患者" width="80" />
        <el-table-column prop="wardName" label="病区" width="120" />
        <el-table-column prop="oldPersonName" label="原陪护人" width="90" />
        <el-table-column prop="oldCertNo" label="原证号" width="150" />
        <el-table-column prop="newPersonName" label="新陪护人" width="90" />
        <el-table-column prop="newCertNo" label="新证号" width="150" />
        <el-table-column prop="handoverReason" label="交接原因" min-width="120" show-overflow-tooltip />
        <el-table-column prop="newApplyStatus" label="新证状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.newApplyStatus === 0 ? 'warning' : row.newApplyStatus === 1 ? 'success' : 'danger'">
              {{ row.newApplyStatus === 0 ? '待审核' : row.newApplyStatus === 1 ? '已通过' : '已拒绝' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="transferTime" label="交接时间" width="170" />
      </el-table>
      <el-pagination v-model:current-page="current" v-model:page-size="size" :total="total"
        layout="total, prev, pager, next" @current-change="loadData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog v-model="transferDialogVisible" title="发起换陪护" width="550px" destroy-on-close>
      <el-form :model="transferForm" label-width="120px" :rules="transferRules" ref="transferFormRef">
        <el-form-item label="原陪护证" prop="oldCertId">
          <el-select v-model="transferForm.oldCertId" placeholder="选择有效陪护证" filterable style="width:100%"
            @change="onOldCertChange">
            <el-option v-for="c in validCerts" :key="c.id"
              :label="`${c.certNo} - ${c.personName}(${c.patientName})`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="交接原因" prop="handoverReason">
          <el-input v-model="transferForm.handoverReason" type="textarea" :rows="2" />
        </el-form-item>
        <el-divider content-position="left">新陪护人信息</el-divider>
        <el-form-item label="姓名" prop="newPersonName">
          <el-input v-model="transferForm.newPersonName" />
        </el-form-item>
        <el-form-item label="身份证号" prop="newIdCard">
          <el-input v-model="transferForm.newIdCard" />
        </el-form-item>
        <el-form-item label="性别" prop="newGender">
          <el-radio-group v-model="transferForm.newGender">
            <el-radio :value="1">男</el-radio>
            <el-radio :value="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="联系电话" prop="newPhone">
          <el-input v-model="transferForm.newPhone" />
        </el-form-item>
        <el-form-item label="与患者关系" prop="newRelation">
          <el-input v-model="transferForm.newRelation" />
        </el-form-item>
        <el-form-item label="身份证有效期" prop="newIdCardExpireDate">
          <el-date-picker v-model="transferForm.newIdCardExpireDate" type="date" value-format="YYYY-MM-DD" style="width:100%" />
        </el-form-item>
        <el-form-item label="住址">
          <el-input v-model="transferForm.newAddress" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="transferDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitTransfer" :loading="submitting">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { transferCompanion, getTransferPage } from '@/api/certificate'
import { getCertificatePage } from '@/api/certificate'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const current = ref(1)
const size = ref(10)
const total = ref(0)
const queryWardId = ref(null)
const queryOldPerson = ref('')
const queryNewPerson = ref('')
const wardList = ref([])
const validCerts = ref([])
const transferDialogVisible = ref(false)
const transferFormRef = ref(null)

const transferForm = ref({
  oldCertId: null, handoverReason: '', newPersonName: '', newIdCard: '',
  newGender: 1, newPhone: '', newRelation: '', newIdCardExpireDate: '', newAddress: ''
})

const transferRules = {
  oldCertId: [{ required: true, message: '请选择原陪护证', trigger: 'change' }],
  handoverReason: [{ required: true, message: '请输入交接原因', trigger: 'blur' }],
  newPersonName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  newIdCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }],
  newGender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  newPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  newRelation: [{ required: true, message: '请输入关系', trigger: 'blur' }],
  newIdCardExpireDate: [{ required: true, message: '请选择身份证有效期', trigger: 'change' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getTransferPage({
      current: current.value, size: size.value,
      wardId: queryWardId.value, oldPersonName: queryOldPerson.value, newPersonName: queryNewPerson.value
    })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

const loadValidCerts = async () => {
  const res = await getCertificatePage({ current: 1, size: 200, certStatus: 1 })
  validCerts.value = res.data.records
}

const showTransferDialog = () => {
  transferForm.value = {
    oldCertId: null, handoverReason: '', newPersonName: '', newIdCard: '',
    newGender: 1, newPhone: '', newRelation: '', newIdCardExpireDate: '', newAddress: ''
  }
  loadValidCerts()
  transferDialogVisible.value = true
}

const onOldCertChange = () => {}

const submitTransfer = async () => {
  await transferFormRef.value.validate()
  submitting.value = true
  try {
    const userInfo = localStorage.getItem('userInfo')
    const user = userInfo ? JSON.parse(userInfo) : {}
    await transferCompanion({ ...transferForm.value, operatorId: user.userId, operatorName: user.realName })
    ElMessage.success('换陪护申请已提交')
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
