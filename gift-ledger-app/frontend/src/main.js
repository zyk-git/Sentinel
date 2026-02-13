import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import 'vant/lib/index.css'
import { Button, Cell, CellGroup, Field, Form, Image as VanImage, Popup, Picker, Toast, Notify, Search, Collapse, CollapseItem, Tag, Empty, Dialog } from 'vant'

const app = createApp(App)
app.use(router)
app.use(Button).use(Cell).use(CellGroup).use(Field).use(Form).use(VanImage)
app.use(Popup).use(Picker).use(Search).use(Collapse).use(CollapseItem).use(Tag).use(Empty)
app.use(Toast).use(Notify).use(Dialog)
app.mount('#app')
