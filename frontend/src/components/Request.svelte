<script>
    import {
        BreadcrumbItem, Button,
        TableBodyCell,
        TableBodyRow
    } from "flowbite-svelte";

    export let request;
    export let entry;
    export let pending = false;
    export let accepted = false;

</script>

<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href="categories/{entry.category_id}/entries/{entry.id}">{entry.name}</BreadcrumbItem>


    </TableBodyCell>
    {#if pending}
        <TableBodyCell>
            <Button outline size="sm" href="">Accept</Button>
            <Button outline color="red" size="sm" href="">Decline</Button>
        </TableBodyCell>
    {/if}
</TableBodyRow>
