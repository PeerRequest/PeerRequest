<script>
    import { Table, TableBody, TableHead, TableHeadCell } from "flowbite-svelte";
</script>


<Table hoverable={true} shadow={true}>
    <TableHead>
        <TableHeadCell>Title of corresponding paper</TableHeadCell>
        <TableHeadCell>Conference of corresponding paper</TableHeadCell>
    </TableHead>
    <TableBody class="divide-y">
        <slot />
    </TableBody>
</Table>