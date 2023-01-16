const config = {
    content: [
        "./src/**/*.{html,js,svelte,ts}",
        "./node_modules/flowbite-svelte/**/*.{html,js,svelte,ts}"
    ],

    theme: {
        extend: {
            colors: {
                primary: {
                    "50": "#3EE6FB",
                    "100": "#3BDAFA",
                    "200": "#38CEF9",
                    "300": "#35C2F9",
                    "400": "#32B6F8",
                    "500": "#2EABF7",
                    "600": "#2B9FF6",
                    "700": "#2893F6",
                    "800": "#2587F5",
                    "900": "#227BF4"
                },
                pdf: {
                    "bg": "#38343c"
                }
            }
        }
    },

    plugins: [
        require("flowbite/plugin")
    ],
    darkMode: "class"
};

module.exports = config;
