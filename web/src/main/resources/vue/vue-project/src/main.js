import { createApp } from 'vue'
//根组件
import router from './router'
import Root from "./components/Root.vue";

const app = createApp(Root)
app.use(router)
app.mount('#app')
//应用实例必须在调用了.mount方法后才会渲染出来。这里只是指明了app组件的渲染位置。
//当应用发起请求时，渲染过程是什么？
//请求页面 请求html，在构建dom树的时候发起请求js、返回js，然后加载到这里的时候完成挂载。然后dom最终完成渲染？
//js的返回则自动将代码加载执行吗？
//以css为例，当遇到link标签的时候，遇到外链，则向服务器发起请求，然后解析css之类的。  js文件也是一样。
//DOM/ CSSOM  构造基本结构，然后渲染树结合二者并剔除不可见内容；然后布局，即获取渲染树的结构、节点位置和大小，然后按照盒子模型一个个排列。
//然后继续绘制。把渲染树以像素的形式画在页面上。
//请求css文件的同时会继续构建dom树。
//如果先返回css和js文件后也会发生阻塞，不能先执行js文件，必须等到cssom构建完成了才能执行js文件，
// 渲染树是需要dom和cssom构建完成了以后才能构建。而js又可以控制css样式。也就是说js影响了cssom的构建？
    //那为什么不是先执行js再构建css？
    //这里构建的cssom并不是最终结果。最终结果是渲染树。cssom是全量的、在没有js影响下的css样式树。
// cssom构建是渲染中一个重要的阻塞因素。
// cssom构建完成之后就可以执行js里面的内容了。js会阻塞html解析，根本原因是影响了dom和cssom的解析。
//  所以就是，运行完js之后，继续构建（因为是脚本）之后的dom树，然后再渲染树布局和绘制。
// 异步js不会影响dom解析，但是还是会卡在cssom解析。
// 但是可以肯定的是，渲染树布局和绘制，肯定在外部请求资源之后。
// 那么，js运行结果，也就是组件，逻辑还是那个需要在渲染树布局之前完成。  是可以的。

