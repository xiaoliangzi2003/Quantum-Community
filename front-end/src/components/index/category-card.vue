<template>
  <el-card class="category-card">
    <el-row v-for="(row, rowIndex) in categoryRows" :key="rowIndex" class="category-row">
      <el-col v-for="(category, colIndex) in row" :key="colIndex" :span="12" class="category-col">
        <img :src="category.icon" alt="Icon" class="category-icon">
        <span>{{ category.name }}</span>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
export default {
  name: 'CategoryCard',
  computed: {
    categoryRows() {
      const rows = [];
      for (let i = 0; i < this.categories.length; i += 2) {
        rows.push(this.categories.slice(i, i + 2));
      }
      return rows;
    }
  },
  data() {
    return {
      categories: [
        { name: '知识', icon: '../../../public/image/icon/knowledge.svg' },
        { name: '科学', icon: '../../../public/image/icon/science.svg' },
        { name: '脑洞', icon: '../../../public/image/icon/airhead.svg' },
        { name: '游戏', icon: '../../../public/image/icon/game.svg' },
        { name: '财经', icon: '../../../public/image/icon/finance.svg' },
        { name: '心理', icon: '../../../public/image/icon/psychology.svg' },
        { name: '故事', icon: '../../../public/image/icon/story.svg' },
        { name: '职场', icon: '../../../public/image/icon/workplace.svg' },
        { name: '旅行', icon: '../../../public/image/icon/travel.svg' },
        { name: '法律', icon: '../../../public/image/icon/law.svg' },
        { name: '国际', icon: '../../../public/image/icon/internation.svg' },
        { name: '美食', icon: '../../../public/image/icon/food.svg' },
        { name: '动画', icon: '../../../public/image/icon/animation.svg' },
        { name: '宠物', icon: '../../../public/image/icon/pet.svg' },
        { name: '时尚', icon: '../../../public/image/icon/fashion.svg' },
        { name: '健康', icon: '../../../public/image/icon/health.svg' },
        { name: '体育', icon: '../../../public/image/icon/sport.svg' },
      ]
    }
  }
}
</script>

<style scoped>
.category-card {
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  position: relative;
  align-items: center;
  overflow: auto;
  width: 80%;
  font-weight: bold;
  font-family: 'Microsoft YaHei', sans-serif;
  margin: auto;
}

.category-row {
  margin-bottom: 10px;
}

.category-col {
  display: flex;
  align-items: center;
}

.category-icon {
  width: 50px;
  height: 50px;
  margin-right: 10px;
  margin-bottom: 20px;
}
</style>