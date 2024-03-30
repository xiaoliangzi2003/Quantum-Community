<template>
  <div>
    <div class="toc">
      <ul>
        <li v-for="item in toc" :key="item.id">
          <a :href="`#${item.id}`">{{ item.text }}</a>
        </li>
      </ul>
    </div>
    <div v-html="markdownContent"></div>
  </div>
</template>

<script>
export default {
  props: {
    toc: {
      type: Array,
      default: () => [],
    },
    markdownContent: {
      type: String,
      default: '',
    },
  },
};
</script>

<style scoped>
.toc {
  position: fixed;
  left: 0;
  top: 0;
  width: 200px;
  height: 100%;
  overflow: auto;
  border-right: 1px solid #ddd;
  padding: 10px;
}
</style>