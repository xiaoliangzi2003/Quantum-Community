<script setup>
import Login from './views/account/Login.vue';
</script>

<template>
  <header>
    <div class="wrapper">
    </div>
  </header>
  <div id="app">
    <router-view />
  </div>
</template>

<style scoped>
header {
  line-height: 1.5;
}

.logo {
  display: block;
  margin: 0 auto 0;
}

@media (min-width: 1024px) {
  header {
    display: flex;
    place-items: center;
    padding-right: 0
  }

  .logo {
    margin: 0 0 0 0;
  }

  header .wrapper {
    display: flex;
    place-items: flex-start;
    flex-wrap: wrap;
  }
}
</style>

