import {error} from "@sveltejs/kit";

/** @type {import("./$types").PageLoad} */
export function load({params}) {
    if (params.category_id && params.bidding_id) {
        return {
            category_id: params.category_id,
            bidding_id: params.bidding_id
        };
    }

    throw error(404, "Not found");
}