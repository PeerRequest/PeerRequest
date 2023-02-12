<script>
    import {
        BreadcrumbItem, Button,
        TableBodyCell,
        TableBodyRow
    } from "flowbite-svelte";


    export let request;

    export let pending = false;
    export let accepted = false;

</script>

<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem>this is a request</BreadcrumbItem>


    </TableBodyCell>
    {#if pending}
        <TableBodyCell>
            <Button outline size="sm" href="">Accept</Button>
            <Button outline color="red" size="sm" href="">Decline</Button>
        </TableBodyCell>
    {/if}
</TableBodyRow>
