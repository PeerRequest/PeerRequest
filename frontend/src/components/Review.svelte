<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"


    export let review;
    export let category = null;

    //TODO: UserController
    export let reviewer = "Chihiro Kaori";
    export let paper = null;
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
            <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}/reviews/{review.id}" >Review #{review.id}</BreadcrumbItem>
        </TableBodyCell>

        {#if reviewer !== null}
            <TableBodyCell>
                <BreadcrumbItem>{reviewer}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if paper !== null}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}/entries/{paper.id}">{paper.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}

        {#if category !== null}
            <TableBodyCell>
                <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
            </TableBodyCell>
        {/if}
    {/if}

</TableBodyRow>