import adapter from "@sveltejs/adapter-static";
import preprocess from "svelte-preprocess";

/** @type {import("@sveltejs/kit").Config} */
const config = {
    // Consult https://github.com/sveltejs/svelte-preprocess
    // for more information about preprocessors
    preprocess: [
        preprocess({
            postcss: true
        })
    ],
    trailingSlash: 'always',

    kit: {
        adapter: adapter({
            pages: '../public',
            fallback: 'index.html'
        }),
    },
};

export default config;
