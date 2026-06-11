<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">陪护证管理</div>
      <div class="header-actions">
        <el-button @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>
    </div>

    <div class="search-bar">
      <el-select v-model="searchForm.wardId" placeholder="选择病区" clearable style="width: 180px">
        <el-option v-for="ward in wardList" :key="ward.id" :label="ward.wardName" :value="ward.id" />
      </el-select>
      <el-select v-model="searchForm.certStatus" placeholder="证件状态" clearable style="width: 140px">
        <el-option label="有效" :value="1" />
        <el-option label="已失效" :value="0" />
      </el-select>
      <el-input v-model="searchForm.patientName" placeholder="患者姓名" clearable style="width: 160px" />
      <el-input v-model="searchForm.personName" placeholder="陪护人员" clearable style="width: 160px" />
      <el-input v-model="searchForm.certNo" placeholder="证件编号" clearable style="width: 180px" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="certNo" label="证件编号" width="180" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="patientNo" label="住院号" width="140" />
      <el-table-column prop="wardName" label="病区" width="120" />
      <el-table-column prop="bedNo" label="床号" width="80" />
      <el-table-column prop="personName" label="陪护人员" width="100" />
      <el-table-column prop="relation" label="关系" width="80" />
      <el-table-column prop="startDate" label="开始日期" width="120" />
      <el-table-column prop="endDate" label="结束日期" width="120" />
      <el-table-column prop="certStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.certStatus === 1 ? 'success' : 'info'">
            {{ row.certStatus === 1 ? '有效' : '已失效' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="issueTime" label="发证时间" width="160" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <div class="table-actions">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.certStatus === 1" type="danger" link @click="handleInvalid(row)">
              失效
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, sizes, prev, pager, next, jumper"
      :total="total" :current-page="page.current" :page-size="page.size" :page-sizes="[10, 20, 50, 100]"
      @current-change="handleCurrentChange" @size-change="handleSizeChange" />

    <el-dialog v-model="detailVisible" title="陪护证详情" width="650px">
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="证件编号">{{ detailData.certNo }}</el-descriptions-item>
        <el-descriptions-item label="证件状态">
          <el-tag :type="detailData.certStatus === 1 ? 'success' : 'info'">
            {{ detailData.certStatus === 1 ? '有效' : '已失效' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="患者姓名">{{ detailData.patientName }}</el-descriptions-item>
        <el-descriptions-item label="住院号">{{ detailData.patientNo }}</el-descriptions-item>
        <el-descriptions-item label="病区">{{ detailData.wardName }}</el-descriptions-item>
        <el-descriptions-item label="床号">{{ detailData.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="陪护人员">{{ detailData.personName }}</el-descriptions-item>
        <el-descriptions-item label="与患者关系">{{ detailData.relation }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ detailData.startDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detailData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="发证人员">{{ detailData.issueUserName }}</el-descriptions-item>
        <el-descriptions-item label="发证时间">{{ detailData.issueTime }}</el-descriptions-item>
        <el-descriptions-item v-if="detailData.certStatus === 0" label="失效原因" :span="2">
          {{ detailData.invalidReason }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.certStatus === 0" label="失效时间">
          {{ detailData.invalidTime }}
        </el-descriptions-item>
        <el-descriptions-item v-if="detailData.certStatus === 0" label="操作人">
          {{ detailData.invalidOperatorName }}
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog v-model="invalidVisible" title="失效陪护证" width="500px">
      <el-alert title="失效后陪护证将无法使用，请确认操作" type="warning" show-icon style="margin-bottom: 20px" />
      <el-form :model="invalidForm" label-width="80px">
        <el-form-item label="证件编号">
          <span>{{ currentCert?.certNo }}</span>
        </el-form-item>
        <el-form-item label="失效原因">
          <el-input v-model="invalidForm.invalidReason" type="textarea" :rows="4" placeholder="请输入失效原因" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="invalidVisible = false">取消</el-button>
        <el-button type="danger" :loading="invalidLoading" @click="submitInvalid">
          确认失效
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCertificatePage, getCertificateDetail, invalidCertificate } from '@/api/certificate'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const invalidLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const wardList = ref([])

const page = reactive({
  current: 1,
  size: 10
})

const searchForm = reactive({
  wardId: null,
  certStatus: null,
  patientName: '',
  personName: '',
  certNo: ''
})

const detailVisible = ref(false)
const detailData = ref(null)

const invalidVisible = ref(false)
const currentCert = ref(null)
const invalidForm = reactive({
  certId: null,
  invalidReason: '',
  operatorId: null,
  operatorName: ''
})

const loadWardList = async () => {
  try {
    const res = await getWardList()
    wardList.value = res.data
  } catch (error) {
    console.error('获取病区列表失败：', error)
  }
}

const loadData = async () => {
  try {
    loading.value = true
    const params = {
      current: page.current,
      size: page.size,
      wardId: searchForm.wardId,
      certStatus: searchForm.certStatus,
      patientName: searchForm.patientName,
      personName: searchForm.personName,
      certNo: searchForm.certNo
    }
    const res = await getCertificatePage(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('加载数据失败：', error)
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchForm.wardId = null
  searchForm.certStatus = null
  searchForm.patientName = ''
  searchForm.personName = ''
  searchForm.certNo = ''
  page.current = 1
  loadData()
}

const handleCurrentChange = (val) => {
  page.current = val
  loadData()
}

const handleSizeChange = (val) => {
  page.size = val
  page.current = 1
  loadData()
}

const viewDetail = async (row) => {
  try {
    const res = await getCertificateDetail(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取详情失败：', error)
  }
}

const handleInvalid = (row) => {
  currentCert.value = row
  invalidForm.certId = row.id
  invalidForm.invalidReason = ''

  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    invalidForm.operatorId = user.userId
    invalidForm.operatorName = user.realName
  }

  invalidVisible.value = true
}

const submitInvalid = async () => {
  try {
    if (!invalidForm.invalidReason.trim()) {
      ElMessage.warning('请输入失效原因')
      return
    }

    await ElMessageBox.confirm('确认失效该陪护证？失效后无法恢复！', '警告', {
      confirmButtonText: '确认',
      cancelButtonText: '取消',
      type: 'warning'
    })

    invalidLoading.value = true
    await invalidCertificate(invalidForm)
    ElMessage.success('失效成功')
    invalidVisible.value = false
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('失败：', error)
    }
  } finally {
    invalidLoading.value = false
  }
}

onMounted(() => {
  loadWardList()
  loadData()
})
</script>

<style scoped>
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
