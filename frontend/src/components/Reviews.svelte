<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";

    export let show_reviewer = false;
    export let show_paper = false;
    export let show_category = false;
    export let show_review_button = false;
</script>

<Table noborder={false} shadow={true}>
    <TableHead>
        {#if show_reviewer}
            <TableHeadCell>Reviewer</TableHeadCell>
        {/if}

        {#if show_paper}
            <TableHeadCell>Research Paper</TableHeadCell>
        {/if}

        {#if show_category}
            <TableHeadCell>Conference</TableHeadCell>
        {/if}

        {#if show_review_button}
            <TableHeadCell></TableHeadCell>
        {/if}

    </TableHead>

    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>