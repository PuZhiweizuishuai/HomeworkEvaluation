<template>
  <v-row justify="center" style="margin: 24px;">
    <v-card
      :color="item.color"
      width="350px"
    >
      <v-card-title class="headline">
        <v-icon>{{ item.icon }}</v-icon>
        <span v-html="'&nbsp;&nbsp;'" />
        {{ item.title }}
      </v-card-title>

      <v-card-subtitle style="height: 4em">{{ item.text }}</v-card-subtitle>

      <v-card-actions>
        <v-spacer />
        <router-link v-if="item.type" :to="item.src">
          <v-btn
            outlined
            rounded
          >
            {{ item.btn }}
          </v-btn>
        </router-link>
        <a v-if="item.type == false" :href="item.src" target="_blank">
          <v-btn
            outlined
            rounded
          >
            {{ item.btn }}
          </v-btn>
        </a>
      </v-card-actions>

    </v-card>
  </v-row>
</template>

<script>
export default {
  props: {
    item: {
      type: Object,
      default: () => {}
    }
  }
}
</script>

<style>

</style>
