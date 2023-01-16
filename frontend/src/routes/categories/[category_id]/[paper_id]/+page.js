import {error} from "@sveltejs/kit";

/** @type {import("./$types").PageLoad} */
export function load({params}) {
    if (params.paper_id && params.category_id) {
        return {
            paper_id: params.paper_id,
            category_id: params.category_id
        };
    }

    throw error(404, "Not found");
}