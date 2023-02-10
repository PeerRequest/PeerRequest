<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import StarRating from "./StarRating.svelte";
    import Skeleton from "svelte-skeleton-loader"


    export let href;
    export let paper;
    export let rating = null;
    export let category = null;
    export let slots = null;
    export let loading = false;

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
            <BreadcrumbItem href="/categories/{category.id}/{paper.id}">{paper.name}</BreadcrumbItem>
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
</TableBodyRow>