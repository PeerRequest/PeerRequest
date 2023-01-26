<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";

    export let category_type;
    export let title;
    export let show_category;
</script>

<Table noborder={false} shadow={true}>
    <TableHead>
        <TableHeadCell>Title</TableHeadCell>

        {#if show_category}
            <TableHeadCell>Conference</TableHeadCell>
        {/if}

        {#if (category_type != null) && (category_type === "Internal") }
            <TableHeadCell>Open Review Slots</TableHeadCell>
            <TableHeadCell></TableHeadCell>
        {/if}

    </TableHead>
    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>