<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";

    export let category_type = "External";
    export let show_category = false;
    export let show_slots = false;
    export let show_rating = false;

</script>

<Table noborder={false} shadow={true}>
    <TableHead>
        <TableHeadCell>Title</TableHeadCell>

        {#if show_category}
            <TableHeadCell>Conference</TableHeadCell>
        {/if}

        <TableHeadCell>Authors</TableHeadCell>

        {#if show_slots}
            <TableHeadCell>Open Review Slots</TableHeadCell>
            <TableHeadCell></TableHeadCell>
        {/if}

        {#if show_rating}
            <TableHeadCell>Rating</TableHeadCell>
        {/if}

    </TableHead>
    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>