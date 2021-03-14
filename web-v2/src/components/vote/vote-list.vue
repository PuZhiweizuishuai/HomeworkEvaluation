<template>
  <v-container>
    <v-row v-for="item in votes" :key="item.id.counter">
      <v-col>
        <!--  -->
        <Card :vote="item" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import Card from '@/components/vote/vote-card.vue'

export default {
  components: {
    Card
  },
  props: {
    votes: {
      type: Array,
      default: () => { return [] }
    }
  }
}
</script>

<style>

</style>
