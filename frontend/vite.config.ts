import {sveltekit} from '@sveltejs/kit/vite';
import type {UserConfig} from 'vite';

const config: UserConfig = {
    plugins: [sveltekit()],
    server: {
        // to use a local backend instance with `npm run dev` we have to proxy all api requests to the backend
        proxy: {
            // proxy for api calls
            '/api': 'http://localhost:8080',
            // proxy for logout requests
            '/logout': 'http://localhost:8080',

            // dummy proxy to trigger the login flow.
            /*
            How-To:
              1. navigate to /dev-login
              2. perform login
              3. Remember to navigate back to the instance running through `npm run dev`
                 because the auth server might send you to the wrong frontend.
             */
            '/dev-login': 'http://localhost:8080',
        },
    },
};

export default config;
