<template>
  <div class="page-container">
    <div class="page-header">
      <div class="page-title">查验记录</div>
      <el-button @click="loadData">
        <el-icon><Refresh /></el-icon>
        刷新
      </el-button>
    </div>

    <div class="search-bar">
      <el-select v-model="searchForm.wardId" placeholder="选择病区" clearable style="width: 180px">
        <el-option v-for="ward in wardList" :key="ward.id" :label="ward.wardName" :value="ward.id" />
      </el-select>
      <el-select v-model="searchForm.gateType" placeholder="出入类型" clearable style="width: 140px">
        <el-option label="入院" :value="1" />
        <el-option label="出院" :value="2" />
      </el-select>
      <el-select v-model="searchForm.checkResult" placeholder="查验结果" clearable style="width: 140px">
        <el-option label="通过" :value="1" />
        <el-option label="拒绝" :value="2" />
      </el-select>
      <el-input v-model="searchForm.personName" placeholder="人员姓名" clearable style="width: 160px" />
      <el-date-picker v-model="searchForm.startDate" type="date" placeholder="开始日期" value-format="YYYY-MM-DD"
        style="width: 160px" />
      <el-date-picker v-model="searchForm.endDate" type="date" placeholder="结束日期" value-format="YYYY-MM-DD"
        style="width: 160px" />
      <el-button type="primary" @click="loadData">查询</el-button>
      <el-button @click="resetSearch">重置</el-button>
    </div>

    <el-table :data="tableData" v-loading="loading" border stripe>
      <el-table-column prop="recordNo" label="记录编号" width="180" />
      <el-table-column prop="certNo" label="陪护证号" width="180" />
      <el-table-column prop="personName" label="人员姓名" width="100" />
      <el-table-column prop="idCard" label="身份证号" width="180" />
      <el-table-column prop="wardName" label="病区" width="120" />
      <el-table-column prop="gateType" label="类型" width="80">
        <template #default="{ row }">
          <el-tag :type="row.gateType === 1 ? 'success' : 'info'">
            {{ row.gateType === 1 ? '入院' : '出院' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="checkResult" label="结果" width="80">
        <template #default="{ row }">
          <el-tag :type="row.checkResult === 1 ? 'success' : 'danger'">
            {{ row.checkResult === 1 ? '通过' : '拒绝' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="refuseReason" label="拒绝原因" min-width="150" show-overflow-tooltip />
      <el-table-column prop="gateUserName" label="操作人" width="100" />
      <el-table-column prop="checkTime" label="查验时间" width="160" />
    </el-table>

    <el-pagination class="pagination" background layout="total, sizes, prev, pager, next, jumper"
      :total="total" :current-page="page.current" :page-size="page.size" :page-sizes="[10, 20, 50, 100]"
      @current-change="handleCurrentChange" @size-change="handleSizeChange" />
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getGateRecords } from '@/api/gate'
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
  wardId: null,
  gateType: null,
  checkResult: null,
  personName: '',
  startDate: '',
  endDate: ''
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
      gateType: searchForm.gateType,
      checkResult: searchForm.checkResult,
      personName: searchForm.personName,
      startDate: searchForm.startDate,
      endDate: searchForm.endDate
    }
    const res = await getGateRecords(params)
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
  searchForm.gateType = null
  searchForm.checkResult = null
  searchForm.personName = ''
  searchForm.startDate = ''
  searchForm.endDate = ''
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
