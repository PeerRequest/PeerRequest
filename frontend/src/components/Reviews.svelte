<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";

</script>

<Table noborder={false} shadow={true}>
    <TableHead>
        <TableHeadCell>Review</TableHeadCell>
        <TableHeadCell>Reviewer</TableHeadCell>
        <TableHeadCell>Research Paper</TableHeadCell>
        <TableHeadCell>Conference</TableHeadCell>

    </TableHead>

    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>