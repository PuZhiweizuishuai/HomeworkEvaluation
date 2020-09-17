<template>
  <v-container fill-height>
    <v-row>
      <v-col cols="3">
        123456
      </v-col>
      <v-col cols="9">
        <v-carousel
          cycle
          height="350"
          hide-delimiter-background
          show-arrows-on-hover
        >
          <v-carousel-item
            v-for="(item, i) in carouselList"
            :key="i"
            :src="item.image"
            :href="item.url"
            target="_blank"
          />
        </v-carousel>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'Home',
  data() {
    return {
      carouselList: []
    }
  },
  created() {
    this.getCarousel()
  },
  methods: {
    getCarousel() {
      this.httpGet('/index/curriculum/ad', (json) => {
        if (json.status === 200) {
          this.carouselList = json.data
        }
      })
    }

  }

}
</script>

<style>

</style>
