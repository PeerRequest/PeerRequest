<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import StarRating from "./StarRating.svelte";
    import Skeleton from "svelte-skeleton-loader"
    import {onMount} from "svelte";


    export let href;
    export let paper = "";
    export let rating = null;
    export let slots = null;
    export let loading = false;
    export let error = null;

    let category = null;

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

    onMount(() => {
        loadCategory()
    })

    export let show_category = false;

</script>


<TableBodyRow>
    {#if loading}
        {#each [...Array(5).keys()] as i}
            <TableBodyCell>
                <div>
                    <Skeleton/>
                </div>
            </TableBodyCell>
        {/each}
    {:else }

        {#if category !== null}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>


            {#if show_category}


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

    {/if}
</TableBodyRow>