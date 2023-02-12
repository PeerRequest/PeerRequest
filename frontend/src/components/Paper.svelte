<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import StarRating from "./StarRating.svelte";
    import Skeleton from "svelte-skeleton-loader"
    import {onMount} from "svelte";


    export let href;
    export let paper = "";
    export let rating = null;
    export let category = null;
    //TODO RequestController OpenSlots
    export let slots = null;
    export let loading = false;
    export let current_user = null;
    export let error = null;

    let reviews = null;
    let isReviewer = false;

    function loadCategory() {
        category = null;
        fetch("/api/categories/" + paper.category_id)
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    category = resp;
                }
            })
            .catch(err => console.log(err))
    }

    function reviewToEntry (review) {

        if (review.entry_id === paper.id) {
            isReviewer = true
        }
    }

    function loadUserReviews() {
        reviews = null;
        fetch("/api/reviews")
            .then(resp => resp.json())
            .then(resp => {
                if (resp.status < 200 || resp.status >= 300) {
                    error = "" + resp.status + ": " + resp.message;
                    console.log(error);
                } else {
                    reviews = resp.content;
                    reviews.forEach(reviewToEntry)
                }
            })
            .catch(err => console.log(err))
    }

    onMount(()=>{
        loadCategory()
        loadUserReviews()
    })

    export let show_category = false;

</script>


<TableBodyRow>
    {#if loading || category === null || paper === null}
        {#each [...Array(5).keys()] as i}
            <TableBodyCell>
                <div>
                    <Skeleton/>
                </div>
            </TableBodyCell>
        {/each}
    {:else }
        <TableBodyCell>
            {#if (current_user.id === paper.researcher_id) || isReviewer }
                <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            {:else }
                <BreadcrumbItem>{paper.name}</BreadcrumbItem>
            {/if}
        </TableBodyCell>

        {#if show_category && category !== null}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>

            {#if category !== null}
                <TableBodyCell>
                    <BreadcrumbItem
                            href="/categories/{category.id}">{category.name}</BreadcrumbItem>
                </TableBodyCell>
            {/if}

            {#if (slots !== null) && (category.label === "INTERNAL")}
                <TableBodyCell>{slots}</TableBodyCell>
                <TableBodyCell>
                    <Button disabled={slots<=0} href={href} outline size="xs">
                        Start Review
                    </Button>
                </TableBodyCell>
            {/if}

            {#if rating !== null}
                <TableBodyCell>
                    <StarRating rating={rating}/>
                </TableBodyCell>
            {/if}

        {/if}

    {/if}
</TableBodyRow>