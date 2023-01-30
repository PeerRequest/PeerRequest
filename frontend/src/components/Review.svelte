<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";

    export let href;
    export let id;
    export let category = null;
    export let reviewer = null;
    export let paper = null;

</script>


<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href={href}>Review #{id}</BreadcrumbItem>
    </TableBodyCell>

    {#if reviewer !== null}
        <TableBodyCell>
            <BreadcrumbItem>{reviewer}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if paper !== null}
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{category.id}/{paper.id}">{paper.title}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if category !== null}
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

</TableBodyRow>