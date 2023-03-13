<script>
    import {Table, TableBody, TableHead, TableHeadCell} from "flowbite-svelte";

    export let accepted = false;
    export let pending = false;
</script>

<Table noborder={false} shadow={true}>
    <TableHead>

        {#if accepted}
            <TableHeadCell>Reviews for Papers</TableHeadCell>
        {:else}
            {#if pending}
                <TableHeadCell>Pending Requests</TableHeadCell>

            {:else}
                <TableHeadCell>Requests</TableHeadCell>
            {/if}
        {/if}

        {#if pending}
            <TableHeadCell></TableHeadCell>
        {/if}

    </TableHead>
    <TableBody class="divide-y">
        <slot></slot>
    </TableBody>
</Table>