<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">护士站审核</div>
      <el-badge :value="pendingCount" class="pending-badge">
        <el-button type="primary" @click="loadData">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </el-badge>
    </div>

    <div class="search-bar">
      <el-select v-model="searchForm.wardId" placeholder="选择病区" clearable style="width: 180px">
        <el-option v-for="ward in wardList" :key="ward.id" :label="ward.wardName" :value="ward.id" />
      </el-select>
      <el-input v-model="searchForm.patientName" placeholder="患者姓名" clearable style="width: 160px" />
      <el-input v-model="searchForm.personName" placeholder="陪护人员姓名" clearable style="width: 160px" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="applyNo" label="申请单号" width="180" />
      <el-table-column prop="patientName" label="患者姓名" width="100" />
      <el-table-column prop="patientNo" label="住院号" width="140" />
      <el-table-column prop="wardName" label="病区" width="120" />
      <el-table-column prop="bedNo" label="床号" width="80" />
      <el-table-column prop="personName" label="陪护人员" width="100" />
      <el-table-column prop="relation" label="关系" width="80" />
      <el-table-column prop="expectedStartDate" label="开始日期" width="120" />
      <el-table-column prop="expectedEndDate" label="结束日期" width="120" />
      <el-table-column prop="applyStatus" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.applyStatus)">
            {{ getStatusText(row.applyStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applyTime" label="申请时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <div class="table-actions">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
            <el-button v-if="row.applyStatus === 0" type="success" link @click="handleAudit(row, 1)">
              通过
            </el-button>
            <el-button v-if="row.applyStatus === 0" type="danger" link @click="handleAudit(row, 2)">
              拒绝
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, sizes, prev, pager, next, jumper"
      :total="total" :current-page="page.current" :page-size="page.size" :page-sizes="[10, 20, 50, 100]"
      @current-change="handleCurrentChange" @size-change="handleSizeChange" />

    <el-dialog v-model="detailVisible" title="申请详情" width="650px">
      <el-descriptions v-if="detailData" :column="2" border>
        <el-descriptions-item label="申请单号">{{ detailData.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag :type="getStatusType(detailData.applyStatus)">
            {{ getStatusText(detailData.applyStatus) }}
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
        <el-descriptions-item label="开始日期">{{ detailData.expectedStartDate }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ detailData.expectedEndDate }}</el-descriptions-item>
        <el-descriptions-item label="申请理由" :span="2">{{ detailData.applyReason }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detailData.auditUserName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detailData.auditTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核意见" :span="2">{{ detailData.auditRemark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div v-if="detailData.applyStatus === 0" style="margin-top: 20px; text-align: right">
        <el-button type="success" @click="confirmAudit(1)">审核通过</el-button>
        <el-button type="danger" @click="confirmAudit(2)">审核拒绝</el-button>
      </div>
    </el-dialog>

    <el-dialog v-model="auditVisible" :title="auditResult === 1 ? '审核通过' : '审核拒绝'" width="500px">
      <el-form :model="auditForm" label-width="80px">
        <el-form-item label="审核意见">
          <el-input v-model="auditForm.auditRemark" type="textarea" :rows="4"
            :placeholder="auditResult === 1 ? '请输入审核意见（选填）' : '请输入拒绝原因'" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="auditVisible = false">取消</el-button>
        <el-button type="primary" :loading="auditing" @click="submitAudit">
          确认
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getApplyPage, getApplyDetail } from '@/api/apply'
import { auditApply, getPendingList } from '@/api/audit'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const auditing = ref(false)
const tableData = ref([])
const total = ref(0)
const pendingCount = ref(0)
const wardList = ref([])

const page = reactive({
  current: 1,
  size: 10
})

const searchForm = reactive({
  wardId: null,
  patientName: '',
  personName: ''
})

const detailVisible = ref(false)
const detailData = ref(null)

const auditVisible = ref(false)
const auditResult = ref(1)
const currentApply = ref(null)
const auditForm = reactive({
  applyId: null,
  auditResult: 1,
  auditRemark: '',
  auditUserId: null,
  auditUserName: ''
})

const loadWardList = async () => {
  try {
    const res = await getWardList()
    wardList.value = res.data
  } catch (error) {
    console.error('获取病区列表失败：', error)
  }
}

const loadPendingCount = async () => {
  try {
    const res = await getPendingList({ current: 1, size: 1, wardId: searchForm.wardId })
    pendingCount.value = res.data.total
  } catch (error) {
    console.error('获取待审核数量失败：', error)
  }
}

const loadData = async () => {
  try {
    loading.value = true
    const params = {
      current: page.current,
      size: page.size,
      wardId: searchForm.wardId,
      applyStatus: null,
      patientName: searchForm.patientName,
      personName: searchForm.personName
    }
    const res = await getApplyPage(params)
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
  searchForm.patientName = ''
  searchForm.personName = ''
  page.current = 1
  loadData()
  loadPendingCount()
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
    const res = await getApplyDetail(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error('获取详情失败：', error)
  }
}

const handleAudit = (row, result) => {
  currentApply.value = row
  auditResult.value = result
  auditForm.applyId = row.id
  auditForm.auditResult = result
  auditForm.auditRemark = ''

  const userInfo = localStorage.getItem('userInfo')
  if (userInfo) {
    const user = JSON.parse(userInfo)
    auditForm.auditUserId = user.userId
    auditForm.auditUserName = user.realName
  }

  if (result === 1) {
    auditVisible.value = true
  } else {
    auditVisible.value = true
  }
}

const confirmAudit = (result) => {
  detailVisible.value = false
  handleAudit(detailData.value, result)
}

const submitAudit = async () => {
  try {
    if (auditResult.value === 2 && !auditForm.auditRemark.trim()) {
      ElMessage.warning('请输入拒绝原因')
      return
    }

    await ElMessageBox.confirm(
      `确认${auditResult.value === 1 ? '通过' : '拒绝'}该申请？`,
      '提示',
      {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
      }
    )

    auditing.value = true
    await auditApply(auditForm)
    ElMessage.success('审核成功')
    auditVisible.value = false
    loadData()
    loadPendingCount()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('审核失败：', error)
    }
  } finally {
    auditing.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    0: '待审核',
    1: '审核通过',
    2: '审核拒绝',
    3: '已失效'
  }
  return map[status] || '未知'
}

const getStatusType = (status) => {
  const map = {
    0: 'warning',
    1: 'success',
    2: 'danger',
    3: 'info'
  }
  return map[status] || 'info'
}

onMounted(() => {
  loadWardList()
  loadData()
  loadPendingCount()
})
</script>

<style scoped>
.pagination {
  margin-top: 20px;
  text-align: right;
}

.pending-badge {
  margin-right: 10px;
}
</style>
