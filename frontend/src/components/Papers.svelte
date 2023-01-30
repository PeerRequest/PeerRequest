<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";

    export let category_type;
    export let category = null;
    export let slots = null;

</script>

<Table noborder={false} shadow={true}>
    <TableHead>
        <TableHeadCell>Title</TableHeadCell>

        {#if category !== null}
            <TableHeadCell>Conference</TableHeadCell>
        {/if}

        {#if (slots !== null) && (category_type === "Internal")}
            <TableHeadCell>Open Review Slots</TableHeadCell>
            <TableHeadCell></TableHeadCell>
        {/if}

    </TableHead>
    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>