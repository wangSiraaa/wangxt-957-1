<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">陪护申请列表</div>
      <el-button type="primary" @click="goSubmit">
        <el-icon><Plus /></el-icon>
        提交申请
      </el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="searchForm.wardId" placeholder="选择病区" clearable style="width: 180px">
        <el-option v-for="ward in wardList" :key="ward.id" :label="ward.wardName" :value="ward.id" />
      </el-select>
      <el-select v-model="searchForm.applyStatus" placeholder="申请状态" clearable style="width: 140px">
        <el-option label="待审核" :value="0" />
        <el-option label="审核通过" :value="1" />
        <el-option label="审核拒绝" :value="2" />
        <el-option label="已失效" :value="3" />
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
      <el-table-column label="操作" width="100" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="viewDetail(row)">查看</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination class="pagination" background layout="total, sizes, prev, pager, next, jumper"
      :total="total" :current-page="page.current" :page-size="page.size" :page-sizes="[10, 20, 50, 100]"
      @current-change="handleCurrentChange" @size-change="handleSizeChange" />

    <el-dialog v-model="detailVisible" title="申请详情" width="600px">
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
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getApplyPage, getApplyDetail } from '@/api/apply'
import { getWardList } from '@/api/ward'

const router = useRouter()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const wardList = ref([])

const page = reactive({
  current: 1,
  size: 10
})

const searchForm = reactive({
  wardId: null,
  applyStatus: null,
  patientName: '',
  personName: ''
})

const detailVisible = ref(false)
const detailData = ref(null)

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
      applyStatus: searchForm.applyStatus,
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
  searchForm.applyStatus = null
  searchForm.patientName = ''
  searchForm.personName = ''
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

const goSubmit = () => {
  router.push('/apply/submit')
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
})
</script>

<style scoped>
.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
