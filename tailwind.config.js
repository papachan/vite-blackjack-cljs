/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./js/*.{js}",
    "./*.{html}"
  ],
  theme: {
    fontFamily: {
      sans: ["Roboto", "sans-serif"]
    }
    extend: {},
  },
  plugins: []
}
