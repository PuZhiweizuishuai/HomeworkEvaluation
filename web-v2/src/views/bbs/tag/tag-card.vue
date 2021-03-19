<template>
  <v-hover v-slot="{ hover }">
    <v-card outlined :class="{ 'on-hover': hover }" :elevation="hover ? 16 : 0">
      <router-link :to="`/bbs/tags/${tag.id}?_t=${new Date().getTime()}`">
        <v-card-title>
          {{ tag.title }}
        </v-card-title>
        <v-card-subtitle>
          {{ tag.descript }}
        </v-card-subtitle>
      </router-link>
    </v-card>
  </v-hover>
</template>

<script>
export default {
  props: {
    tag: {
      type: Object,
      default: null
    }
  }
}
</script>

<style>

</style>
