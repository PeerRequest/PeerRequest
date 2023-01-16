import {error} from "@sveltejs/kit";

/** @type {import('./$types').PageLoad} */
export function load({params}) {
    if (params.category_id) {
        return {
            category_id: params.category_id
        };
    }

    throw error(404, 'Not found');
}