import { useEffect, useState } from "react"
import axios from "axios"
import { API_BASE } from "../config/api"

function Home() {
  const [message, setMessage] = useState("")

  useEffect(() => {
    axios.get(`${API_BASE}/test`)
      .then(response => {
        setMessage(response.data)
      })
      .catch(error => {
        console.error("Error:", error)
      })
  }, [])

  return (
    <div>
      <h2>React Frontend</h2>
      <p>{message}</p>
    </div>
  )
}

export default Home