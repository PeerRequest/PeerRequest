<script>
    import {BreadcrumbItem, Button, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import Skeleton from "svelte-skeleton-loader"

    export let category = null;
    export let loading = false;

    function map_type(type) {
        switch (type) {
            case "INTERNAL":
                return "Internal";
            case "EXTERNAL":
                return "External";
        }
    }

    function map_deadline(deadline) {
        if (deadline != null) {
            return new Date(Date.parse(deadline)).toLocaleDateString();
        } else {
            return "not specified"
        }
    }
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
            <BreadcrumbItem href="/categories/{category.id}">{category.name}</BreadcrumbItem >
        </TableBodyCell>
        <TableBodyCell>{map_type(category.label)}</TableBodyCell>
        <TableBodyCell>{category.year}</TableBodyCell>
        <TableBodyCell>{map_deadline(category.deadline) }</TableBodyCell>

        <TableBodyCell>
            <Button href="/categories/{category.id}" outline size="xs">Papers</Button>
        </TableBodyCell>
    {/if}
</TableBodyRow>
