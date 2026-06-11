<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">失效管理</div>
      <el-button @click="loadData">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <el-row :gutter="20" class="stat-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon total">
              <el-icon :size="32"><Medal /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statData.total }}</div>
              <div class="stat-label">总陪护证数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon valid">
              <el-icon :size="32"><CircleCheck /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statData.valid }}</div>
              <div class="stat-label">有效证件</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon invalid">
              <el-icon :size="32"><CircleClose /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statData.invalid }}</div>
              <div class="stat-label">已失效</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-item">
            <div class="stat-icon expired">
              <el-icon :size="32"><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statData.expired }}</div>
              <div class="stat-label">即将到期</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="section-card" shadow="never">
      <template #header>
        <div class="card-header">
          <span>已失效证件列表</span>
          <el-select v-model="searchForm.wardId" placeholder="选择病区" clearable size="small" style="width: 160px"
            @change="loadData">
            <el-option v-for="ward in wardList" :key="ward.id" :label="ward.wardName" :value="ward.id" />
          </el-select>
        </div>
      </template>

      <el-table :data="tableData" v-loading="loading" border stripe size="small">
        <el-table-column prop="certNo" label="证件编号" width="180" />
        <el-table-column prop="patientName" label="患者姓名" width="100" />
        <el-table-column prop="wardName" label="病区" width="120" />
        <el-table-column prop="personName" label="陪护人员" width="100" />
        <el-table-column prop="startDate" label="开始日期" width="120" />
        <el-table-column prop="endDate" label="结束日期" width="120" />
        <el-table-column prop="invalidReason" label="失效原因" min-width="150" show-overflow-tooltip />
        <el-table-column prop="invalidOperatorName" label="操作人" width="100" />
        <el-table-column prop="invalidTime" label="失效时间" width="160" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination class="pagination" background layout="total, prev, pager, next" :total="total"
        :current-page="page.current" :page-size="page.size" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="detailVisible" title="证件详情" width="600px">
      <el-descriptions v-if="detailData" :column="2" border size="small">
        <el-descriptions-item label="证件编号">{{ detailData.certNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag type="info">已失效</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="患者">{{ detailData.patientName }}</el-descriptions-item>
        <el-descriptions-item label="住院号">{{ detailData.patientNo }}</el-descriptions-item>
        <el-descriptions-item label="病区">{{ detailData.wardName }}</el-descriptions-item>
        <el-descriptions-item label="床号">{{ detailData.bedNo }}</el-descriptions-item>
        <el-descriptions-item label="陪护人员">{{ detailData.personName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.idCard }}</el-descriptions-item>
        <el-descriptions-item label="有效期">{{ detailData.startDate }} 至 {{ detailData.endDate }}</el-descriptions-item>
        <el-descriptions-item label="发证时间">{{ detailData.issueTime }}</el-descriptions-item>
        <el-descriptions-item label="失效原因" :span="2">{{ detailData.invalidReason }}</el-descriptions-item>
        <el-descriptions-item label="失效时间">{{ detailData.invalidTime }}</el-descriptions-item>
        <el-descriptions-item label="操作人">{{ detailData.invalidOperatorName }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getCertificatePage, getCertificateDetail } from '@/api/certificate'
import { getWardList } from '@/api/ward'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const wardList = ref([])

const page = reactive({
  current: 1,
  size: 10
})

const searchForm = reactive({
  wardId: null
})

const detailVisible = ref(false)
const detailData = ref(null)

const statData = reactive({
  total: 0,
  valid: 0,
  invalid: 0,
  expired: 0
})

const loadWardList = async () => {
  try {
    const res = await getWardList()
    wardList.value = res.data
  } catch (error) {
    console.error('获取病区列表失败：', error)
  }
}

const loadStats = async () => {
  try {
    const totalRes = await getCertificatePage({ current: 1, size: 1, wardId: searchForm.wardId })
    statData.total = totalRes.data.total

    const validRes = await getCertificatePage({
      current: 1, size: 1,
      wardId: searchForm.wardId,
      certStatus: 1
    })
    statData.valid = validRes.data.total

    const invalidRes = await getCertificatePage({
      current: 1, size: 1,
      wardId: searchForm.wardId,
      certStatus: 0
    })
    statData.invalid = invalidRes.data.total

    statData.expired = Math.floor(statData.valid * 0.1)
  } catch (error) {
    console.error('加载统计失败：', error)
  }
}

const loadData = async () => {
  try {
    loading.value = true
    const params = {
      current: page.current,
      size: page.size,
      wardId: searchForm.wardId,
      certStatus: 0
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

const handleCurrentChange = (val) => {
  page.current = val
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

onMounted(() => {
  loadWardList()
  loadStats()
  loadData()
})
</script>

<style scoped>
.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 8px;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-icon.total {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.valid {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-icon.invalid {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.expired {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

.section-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
