<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"

    export let review;
    export let category = null;

    //TODO: UserController
    export let reviewer = "Chihiro Kaori";
    export let paper = null;
    export let loading = false;

    export let show_category = false;
    export let show_paper = false;
    export let show_reviewer = false;

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
            <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}/reviews/{review.id}" >Review #{review.id}</BreadcrumbItem>
        </TableBodyCell>

        {#if show_reviewer}
            <TableBodyCell>
                <BreadcrumbItem>{reviewer}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_paper}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{paper.category_id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if show_category}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}
    {/if}

</TableBodyRow>