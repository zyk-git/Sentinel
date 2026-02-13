<template>
  <div class="page">
    <van-cell-group inset title="活动信息设置">
      <van-field v-model="activity.title" label="标题" />
      <van-field v-model="activity.date" label="日期" />
      <van-field v-model="activity.location" label="地点" />
      <van-cell title="微信收款码">
        <template #value><input type="file" accept="image/*" @change="e => wxFile = e.target.files[0]" /></template>
      </van-cell>
      <van-image v-if="activity.wxQrcodePath" width="100%" :src="activity.wxQrcodePath" />
      <van-cell title="支付宝收款码">
        <template #value><input type="file" accept="image/*" @change="e => aliFile = e.target.files[0]" /></template>
      </van-cell>
      <van-image v-if="activity.aliQrcodePath" width="100%" :src="activity.aliQrcodePath" />
      <div class="p16"><van-button block type="primary" @click="saveActivity">保存活动信息</van-button></div>
    </van-cell-group>

    <van-cell-group inset title="礼金记录管理">
      <van-search v-model="keyword" placeholder="按姓名/代付人搜索" @search="loadRecords" @clear="loadRecords" />
      <div class="stat">总金额：{{ totalAmount }} 元</div>
      <div class="btns">
        <van-button size="small" type="primary" @click="loadRecords">刷新</van-button>
        <van-button size="small" @click="exportExcel">导出Excel</van-button>
        <van-button size="small" type="danger" plain @click="clearAll">清空全部</van-button>
      </div>
      <van-empty v-if="groups.length === 0" description="暂无数据" />
      <van-collapse v-else v-model="activeNames">
        <van-collapse-item v-for="group in groups" :key="group.submitId" :name="group.submitId">
          <template #title>
            {{ group.payerName }}（{{ group.items.length }}条）
            <van-tag type="primary" style="margin-left: 6px">{{ group.groupTotal }}元</van-tag>
          </template>
          <div v-for="item in group.items" :key="item.id" class="record">
            <div>{{ item.submitTime }} | {{ item.name }} | ¥{{ item.amount }} | {{ item.relation }}</div>
            <div class="blessing">{{ item.blessing || '无祝福语' }}</div>
            <van-button size="mini" type="danger" plain @click="remove(item.id)">删除</van-button>
          </div>
        </van-collapse-item>
      </van-collapse>
    </van-cell-group>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { Notify, Dialog } from 'vant'
import http from '../api/http'

const activity = ref({})
const records = ref([])
const keyword = ref('')
const totalAmount = ref(0)
const activeNames = ref([])
let wxFile = null
let aliFile = null

const groups = computed(() => {
  const map = new Map()
  records.value.forEach(item => {
    if (!map.has(item.submitId)) {
      map.set(item.submitId, { submitId: item.submitId, payerName: item.payerName, items: [], groupTotal: 0 })
    }
    const g = map.get(item.submitId)
    g.items.push(item)
    g.groupTotal += Number(item.amount)
  })
  return [...map.values()]
})

const loadActivity = async () => {
  const { data } = await http.get('/api/activity')
  activity.value = data
}

const loadRecords = async () => {
  try {
    const { data } = await http.get('/api/records', { params: { keyword: keyword.value } })
    records.value = data.list || []
    totalAmount.value = data.totalAmount || 0
  } catch (e) {
    Notify({ type: 'danger', message: '请先登录后台' })
  }
}

const saveActivity = async () => {
  const formData = new FormData()
  formData.append('title', activity.value.title || '')
  formData.append('date', activity.value.date || '')
  formData.append('location', activity.value.location || '')
  if (wxFile) formData.append('wxQrcode', wxFile)
  if (aliFile) formData.append('aliQrcode', aliFile)
  await http.post('/api/activity/update', formData)
  Notify({ type: 'success', message: '保存成功' })
  wxFile = null
  aliFile = null
  await loadActivity()
}

const exportExcel = () => {
  const url = keyword.value ? `/api/records/export?keyword=${encodeURIComponent(keyword.value)}` : '/api/records/export'
  window.open(url)
}

const remove = async (id) => {
  await http.delete(`/api/records/${id}`)
  Notify({ type: 'success', message: '删除成功' })
  await loadRecords()
}

const clearAll = async () => {
  await Dialog.confirm({ title: '确认', message: '确定清空所有礼金记录？此操作不可恢复。' })
  await http.delete('/api/records/clear')
  Notify({ type: 'success', message: '已清空' })
  await loadRecords()
}

onMounted(async () => {
  await loadActivity()
  await loadRecords()
})
</script>

<style scoped>
.page { padding: 12px; }
.p16 { padding: 16px; }
.btns { display: flex; gap: 8px; padding: 8px 12px; }
.stat { padding: 0 12px 8px; color: #1989fa; font-weight: 700; }
.record { border: 1px solid #f0f0f0; border-radius: 8px; padding: 8px; margin: 8px 0; }
.blessing { color: #666; margin: 6px 0; }
</style>
