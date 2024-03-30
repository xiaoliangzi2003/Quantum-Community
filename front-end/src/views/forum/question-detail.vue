<template>
  <el-container direction="vertical" class="index-container" >
    <Navigation/>
    <el-header class="header">
      <QuestionCard  questionId="$route.params.id"/>
    </el-header>
    <el-container class="main-container">
      <el-aside width="20%" class="left-aside">
      </el-aside>
      <el-main>
        <Answers class="answer-container" questionId="$route.params.id"/>
      </el-main>
      <el-aside width="20%" >
      </el-aside>
    </el-container>
  </el-container>
</template>
<script>
import axios from "axios";
import Questions from "../../components/faq/Questions.vue"
import Navigation from "../../components/index/header.vue"
import Carousel from "../../components/index/banner.vue"
import CategoryCard from "../../components/index/category-card.vue"
import HotList from "../../components/index/hot-list.vue"
import CreationCenter from '../../components/index/creation-center.vue';
import QuestionCard from "../../components/faq/question-card.vue";
import Answers from "../../components/faq/Answers.vue";
export default {
  name: 'Index',
  components: {QuestionCard, Navigation, Questions,Carousel,CategoryCard,HotList,CreationCenter,Answers},
  data() {
    return {
      showCategoryCard: false,
    }
  },
  mounted() {
    window.addEventListener('scroll', this.handleScroll); // 添加滚动事件监听器
  },
  beforeDestroy() {
    window.removeEventListener('scroll', this.handleScroll); // 移除滚动事件监听器
  },
  methods: {
    handleScroll() {
      const carouselHeight = document.querySelector('.header').offsetHeight;
      this.showCategoryCard = window.pageYOffset > carouselHeight;
    },
  },
}
</script>

<style scoped>
.category-card {
  position: relative;
  top: 0; /* Add this line */
  margin-top:20px;
}

.main-container{
  position: relative;
  z-index: 2;
}

.question-container {
  position: relative;
  top: 0;
}

.index-container{
  overflow-x: hidden;
}
navigation{
  margin-bottom: 0;
}
el-container {
  padding: 0;
  margin: 0;
}

Carousel {
  padding: 0;
  margin: 0;
}

.header{
  position: relative;
  width: 100vw;
  height: 300px;
  padding:0;
  margin: -2px 0 0;
  z-index: 1;
}


.index-container {
  height: 100vh;
  width: 100%;
}

el-menu {
  background-color: black;
}

el-menu-item {
  color: black;
}


el-carousel {
  position: relative;
  height: 300px; /* 你可以根据需要调整这个值 */
}
</style>