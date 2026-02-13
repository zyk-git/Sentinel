<template>
  <div class="page">
    <van-cell-group inset>
      <van-cell :title="activity.title || '活动标题'" :label="`${activity.date || ''} ${activity.location || ''}`" />
    </van-cell-group>

    <van-cell-group inset title="收款码">
      <van-cell title="微信收款码" />
      <div class="qrcode-wrap"><van-image width="100%" fit="contain" :src="activity.wxQrcodePath || ''" /></div>
      <van-cell title="支付宝收款码" />
      <div class="qrcode-wrap"><van-image width="100%" fit="contain" :src="activity.aliQrcodePath || ''" /></div>
    </van-cell-group>

    <van-cell-group inset title="礼金登记">
      <div v-for="(item, index) in form.items" :key="index" class="line-card">
        <van-field v-model="item.name" label="姓名" placeholder="请输入姓名" required />
        <van-field v-model.number="item.amount" label="金额" type="number" placeholder="请输入金额" required />
        <van-field label="关系" :value="item.relation" readonly is-link @click="openPicker(index)" required />
        <van-field v-model="item.blessing" label="祝福语" placeholder="可选" />
      </div>
      <div class="btn-row">
        <van-button size="small" @click="addLine" :disabled="form.items.length >= 10">添加一行</van-button>
        <van-button size="small" plain type="danger" @click="removeLine" :disabled="form.items.length <= 1">删除一行</van-button>
      </div>
      <van-field v-model="form.payerName" label="代付人" placeholder="必填" required />
      <van-field v-model="form.payerPhone" label="手机号" type="tel" placeholder="可选" />
      <div class="total">总金额：{{ totalAmount }} 元</div>
      <van-button type="primary" block :disabled="submitted" @click="submit">提交</van-button>
      <div v-if="successText" class="success">{{ successText }}</div>
    </van-cell-group>

    <van-popup v-model:show="pickerVisible" position="bottom">
      <van-picker :columns="relations" @confirm="onPick" @cancel="pickerVisible = false" />
    </van-popup>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { Toast, Notify } from 'vant'
import http from '../api/http'

const activity = ref({})
const relations = ['亲戚', '同学', '同事', '朋友', '其他']
const pickerVisible = ref(false)
const currentIndex = ref(0)
const submitted = ref(false)
const successText = ref('')

const form = reactive({
  payerName: '',
  payerPhone: '',
  items: [{ name: '', amount: '', relation: '亲戚', blessing: '' }]
})

const totalAmount = computed(() => form.items.reduce((sum, i) => sum + (Number(i.amount) || 0), 0).toFixed(2))

const loadActivity = async () => {
  const { data } = await http.get('/api/activity')
  activity.value = data
}

const addLine = () => {
  if (form.items.length < 10) form.items.push({ name: '', amount: '', relation: '亲戚', blessing: '' })
}

const removeLine = () => {
  if (form.items.length > 1) form.items.pop()
}

const openPicker = (index) => {
  currentIndex.value = index
  pickerVisible.value = true
}

const onPick = ({ selectedValues }) => {
  form.items[currentIndex.value].relation = selectedValues[0]
  pickerVisible.value = false
}

const validate = () => {
  if (!form.payerName) return '请填写代付人姓名'
  for (const item of form.items) {
    if (!item.name) return '请填写姓名'
    if (!item.amount || Number(item.amount) <= 0) return '请填写有效金额'
  }
  return ''
}

const submit = async () => {
  const msg = validate()
  if (msg) return Notify({ type: 'warning', message: msg })
  try {
    const payload = {
      payerName: form.payerName,
      payerPhone: form.payerPhone,
      items: form.items.map(i => ({ ...i, amount: Number(i.amount) }))
    }
    const { data } = await http.post('/api/records', payload)
    if (data.message !== '记录成功') {
      return Notify({ type: 'danger', message: data.message || '提交失败' })
    }
    submitted.value = true
    successText.value = `记录成功！请扫码转账总金额 ${data.total} 元给主人`
    Toast.success('提交成功')
  } catch (e) {
    Notify({ type: 'danger', message: e.response?.data?.message || '提交失败，请稍后再试' })
  }
}

onMounted(loadActivity)
</script>

<style scoped>
.page { padding: 12px; }
.qrcode-wrap { padding: 0 16px 16px; }
.line-card { background: #fff; border-radius: 8px; margin: 8px 0; }
.btn-row { display: flex; gap: 8px; margin: 8px 0 16px; }
.total { padding: 12px 0; font-size: 16px; font-weight: 600; color: #1989fa; }
.success { margin-top: 12px; color: #07c160; font-weight: 600; }
</style>
