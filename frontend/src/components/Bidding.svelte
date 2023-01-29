<script>
    import {BreadcrumbItem, TableBodyCell, TableBodyRow} from "flowbite-svelte";
    import StarRating from 'svelte-star-rating';

    export let href;
    export let title;

    const style = 'border: 1px solid firebrick;padding: 12px;';
    const rating =4;
    const config = {
        emptyColor: 'hsl(240, 80%, 85%)',
        fullColor: '#f05',
        showText: true,
        size: 42,
    };
</script>


<TableBodyRow>
    <TableBodyCell>
        <BreadcrumbItem href={href}>{title}</BreadcrumbItem>
    </TableBodyCell>
    <TableBodyCell>
        <StarRating {config} {rating} {style}/>
    </TableBodyCell>
</TableBodyRow>
