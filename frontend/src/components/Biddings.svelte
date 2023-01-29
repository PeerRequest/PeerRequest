<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";
</script>

<Table noborder={false} shadow={true}>
    <TableHead>
        <TableHeadCell>Title</TableHeadCell>
        <TableHeadCell>Rating</TableHeadCell>
    </TableHead>
    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>