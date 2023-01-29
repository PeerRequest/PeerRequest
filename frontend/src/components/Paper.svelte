<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";

    export let href;
    export let title;
    export let id;
    export let category = null;
    export let slots = null;
</script>


<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href="/categories/{category.id}/{id}">{title}</BreadcrumbItem>
    </TableBodyCell>

    {#if category !== null}
        <TableBodyCell>
            <BreadcrumbItem
                    href="/categories/{category.id}">{category.name}</BreadcrumbItem>
        </TableBodyCell>
    {/if}


    {#if (slots !== null) && (category.type === "Internal")}
        <TableBodyCell>{slots}</TableBodyCell>
        <TableBodyCell>
            <Button disabled={slots<=0} href={href} outline size="xs">
                Start Review
            </Button>
        </TableBodyCell>
    {/if}


</TableBodyRow>