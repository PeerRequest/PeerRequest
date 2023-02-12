<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import StarRating from "./StarRating.svelte";
    import Skeleton from "svelte-skeleton-loader"


    export let href;
    export let paper;
    export let rating = null;
    export let category = null;
    //TODO RequestController OpenSlots
    export let slots = null;
    export let loading = false;
    export let current_user = null;

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
        <TableBodyCell>
            {#if current_user.id === paper.researcher_id}
                <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            {:else }
                <BreadcrumbItem>{paper.name}</BreadcrumbItem>
            {/if}
        </TableBodyCell>

        {#if show_category && category !== null}
            <TableBodyCell>
                <BreadcrumbItem
                        href="/categories/{category.id}">{category.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if (slots !== null) && (category.label === "INTERNAL")}
            <TableBodyCell>{slots}</TableBodyCell>
            <TableBodyCell>
                <Button disabled={slots<=0} outline size="xs"> <!--href={href}-->
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
</TableBodyRow>