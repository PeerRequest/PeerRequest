<script>
    import { BreadcrumbItem, TableBodyCell, TableBodyRow } from "flowbite-svelte";
    export let myPaper;
</script>

<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href="/categories/{myPaper.category.id}/{myPaper.id}">{myPaper.title}</BreadcrumbItem>
    </TableBodyCell>
    <TableBodyCell>
        <BreadcrumbItem href="/categories/{myPaper.category.id}">{myPaper.category.name}</BreadcrumbItem>
    </TableBodyCell>
    <!-- <TableBodyCell>{myPaper.category.year}</TableBodyCell> -->
</TableBodyRow>