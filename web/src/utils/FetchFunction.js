const SERVER_API_URL = 'http://127.0.0.1:8081/api'
const fetchApi = {
  get(url, XSRF_TOKEN) {
    fetch(SERVER_API_URL + url, {
      headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        'X-XSRF-TOKEN': XSRF_TOKEN

      },
      method: 'GET',
      credentials: 'include'
    }).then(response => response.json())
      .then(json => {
        return json
      })
      .catch(e => {
        return null
      })
  },
  post(data, url, XSRF_TOKEN) {
    fetch(SERVER_API_URL + url, {
      headers: {
        'Content-Type': 'application/json; charset=UTF-8',
        'X-XSRF-TOKEN': XSRF_TOKEN
      },
      method: 'POST',
      credentials: 'include',
      body: JSON.stringify(data)
    }).then(response => response.json())
      .then(json => {
        return json
      })
      .catch(e => {
        return null
      })
  }
}

export default fetchApi
