import {error} from "@sveltejs/kit";

/** @type {import("./$types").PageLoad} */
export function load({params}) {
    if (params.paper_id && params.category_id && params.review_id) {
        return {
            paper_id: params.paper_id,
            category_id: params.category_id,
            review_id: params.review_id
        };
    }

    throw error(404, "Not found");
}