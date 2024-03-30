<template>
  <el-carousel height="400px">
    <el-carousel-item>
      <img src="../../../public/image/banner/1.jpg" alt="Image 1">
    </el-carousel-item>
    <el-carousel-item>
      <img src="../../../public/image/banner/2.png" alt="Image 2">
    </el-carousel-item>
    <el-carousel-item>
      <img src="../../../public/image/banner/3.jpg" alt="Image 3">
    </el-carousel-item>
    <el-carousel-item>
      <img src="../../../public/image/banner/4.jpg" alt="Image 4">
    </el-carousel-item>
  </el-carousel>
</template>

<script>
export default {
  name: 'Carousel',
}
</script>

<style scoped>
img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
</style>