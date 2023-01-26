<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";

    export let href;
    export let id;
    export let reviewer;
    export let paper;
    export let show_paper = false;
    export let show_reviewer = false;
    export let show_category = false;

</script>


<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href={href}>Review #{id}</BreadcrumbItem>
    </TableBodyCell>

    {#if show_reviewer}
        <TableBodyCell>
            <BreadcrumbItem>{reviewer}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if show_paper}
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{paper.category.id}/{paper.id}">{paper.title}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

    {#if show_category}
        <TableBodyCell>
            <BreadcrumbItem href="/categories/{paper.category.id}">{paper.category.name}</BreadcrumbItem>
        </TableBodyCell>
    {/if}

</TableBodyRow>