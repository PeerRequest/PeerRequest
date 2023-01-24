<script>
    import { Table, TableBody, TableHead, TableHeadCell } from "flowbite-svelte";
</script>


<Table hoverable={true} shadow={true}>
    <TableHead>
        <TableHeadCell>Title</TableHeadCell>
        <TableHeadCell>Conference</TableHeadCell>
    </TableHead>
    <TableBody class="divide-y">
        <slot />
    </TableBody>
</Table>